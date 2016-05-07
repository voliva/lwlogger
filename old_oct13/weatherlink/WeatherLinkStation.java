/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

import java.awt.image.BufferedImage;
import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author victor
 */
public abstract class WeatherLinkStation implements StationFetcher {
    protected boolean registerGust = true;
    
    @Override
    public Dada fetch() {
        BufferedImage speed = Globals.getImage(getSpeedURL());
        if(speed == null) return null;
        
        Dada ret = new Dada();
        OCR ocrSpeed = new OCR(speed);
        
        ret.wind = ocrSpeed.recognizeDecimal(81, 136);
        if(registerGust)
            ret.gust = ocrSpeed.recognizeDecimal(105, 161);
        
        if(ret.wind == null && ret.gust == null) return null;
        
        BufferedImage dir = Globals.getImage(getDirectionURL());
        
        if(dir != null){
            OCR ocrDirection = new OCR(dir);
            ret.dir = ocrDirection.recognizeDirection();
        }
        
        ret.time = (Calendar)Calendar.getInstance().clone();
        
        return ret;
    }

    @Override
    public abstract String getStationCode();
    
    protected abstract String getSpeedURL();
    protected abstract String getDirectionURL();
    
    private static class OCR {
        private final BufferedImage img;
        
        public OCR(BufferedImage img){
            this.img = img;
        }
        
        // Recognize direction
        public Integer recognizeDirection(){
            
            boolean quit = false;
            int x=0;
            int y=0;
            for(int i=0; i<4 && !quit; i++){
                x = 96;
                y = 93;
                
		// 1. Anar tirant fins que trobem un vermell
                while(!isRed(x, y) && checkBounds(x, y)){
                    switch(i){
                        case 0:
                            x--;
                            break;
                        case 1:
                            x++;
                            break;
                        case 2:
                            y++;
                            break;
                        default:
                            y--;
                            break;
                    }
                }
                
                // 2. Anar tirant fins que no sigui vermell
                while(isRed(x, y) && checkBounds(x, y)){
                    switch(i){
                        case 0:
                            x--;
                            break;
                        case 1:
                            x++;
                            break;
                        case 2:
                            y++;
                            break;
                        default:
                            y--;
                            break;
                    }
                }
                
                Integer[] res = goUp(x, y);
                if(res != null && res[2] > 50){
                    quit = true;
                    x = res[0];
                    y = res[1];
                }
            }
            
            if(quit){
                Integer ret;
                x = x - 96;
                y = 93 - y;
                
                if(y == 0){
                    if(x == 0)
                        ret = 360-90;
                    else
                        ret = 90;
                }else
                    ret = (int)Math.toDegrees(Math.atan((float)x/(float)y));
                
                if(y < 0)
                    ret = 180 + ret;
                if(ret < 0)
                    ret = ret + 360;
                
                
                return ret;
            }
            
            return null;
        }
        
        private Integer[] goUp(int x, int y){
            boolean quit = false;
            Integer dist = getDistance(x, y);
            while(!quit){
                Integer[] d = new Integer[9];
                for(int _x=-1; _x<=1; _x++){
                    for(int _y=-1; _y<=1; _y++){
                        if(isRed(x+_x, y+_y))
                            d[(_x+1)*3 + (_y+1)] = getDistance(x+_x, y+_y);
                        else
                            d[(_x+1)*3 + (_y+1)] = null;
                    }
                }
                
                Integer max = 0;
                int iMax = -1;
                for(int i=0; i<(1+1)*3 + (1+1); i++){
                    if(d[i] != null && max < d[i]){
                        max = d[i];
                        iMax = i;
                    }
                }
                
                if(max == dist){
                    quit = true;
                }else{
                    int _x = (int)(iMax / 3) - 1;
                    int _y = (int)(iMax % 3) - 1;
                    
                    dist = max;
                    x = x + _x;
                    y = y + _y;
                }
            }
            Integer[] ret = new Integer[3];
            ret[0] = x;
            ret[1] = y;
            ret[2] = dist;
            
            return ret;
            
        }
        
        private boolean checkBounds(int x, int y){
            return x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight();
        }
        
        private Integer getDistance(int x, int y){
            return Math.abs(x - 96) + Math.abs(y - 93);
        }
        
        // Recognize numbers
        public Double recognizeDecimal(int x, int y){
            Integer deca = recognizeDigit(x, y);
            if(deca == null){
                deca = 0;
                x += 4;
            }else
                x += 8;
            
            Integer uni = recognizeDigit(x, y);
            x += 16;
            Integer deci = recognizeDigit(x, y);
            
            if(uni == null || deci == null) return null;
            
            return deca*10 + uni + deci.doubleValue()/10;
        }
        
        private Integer recognizeDigit(int x, int y){
            if(fits0(x, y)) return 0;
            if(fits1(x, y)) return 1;
            if(fits2(x, y)) return 2;
            if(fits4(x, y)) return 4;

            if(fits6(x, y)) return 6;
            if(fits7(x, y)) return 7;
            if(fits9(x, y)) return 9;

            if(fits5(x, y)) return 5;
            if(fits8(x, y)) return 8;

            if(fits3(x, y)) return 3;

            return null;
        }
        
        private boolean fits0(int x, int y){
            return isBlack(x+1, y+8) && isBlack(x, y+7);
        }
        private boolean fits1(int x, int y){
            return isBlack(x+1, y+2) && isBlack(x+1, y+9);
        }
        private boolean fits2(int x, int y){
            return isBlack(x, y+9);
        }
        private boolean fits3(int x, int y){
            return isBlack(x, y+1);
        }
        private boolean fits4(int x, int y){
            return isBlack(x+1, y+3) && isBlack(x, y+4);
        }
        private boolean fits5(int x, int y){
            return isBlack(x, y);
        }
        private boolean fits6(int x, int y){
            return isBlack(x+1, y+1) && isBlack(x, y+2);
        }
        private boolean fits7(int x, int y){
            return isBlack(x+3, y+6) && isBlack(x, y);
        }
        private boolean fits8(int x, int y){
            return isBlack(x, y+5);
        }
        private boolean fits9(int x, int y){
            return isBlack(x+4, y+8) && isBlack(x+5, y+4) && isBlack(x, y+1);
        }
        
        // Globals
        private boolean isBlack(int x, int y){
            if(!checkBounds(x, y)) return false;
            return img.getRGB(x, y) == 0xFF000000;
        }
        private boolean isRed(int x, int y){
            if(!checkBounds(x, y)) return false;
            return img.getRGB(x, y) == 0xFFFF0000;
        }
    }
}
