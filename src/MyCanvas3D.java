import javax.imageio.ImageIO;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DepthComponentInt;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Raster;
import javax.vecmath.Point3f;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyCanvas3D extends Canvas3D {

    public MyCanvas3D(GraphicsConfiguration graphicsConfiguration, boolean offscrean){
        super(graphicsConfiguration,offscrean);
        this.setDoubleBufferEnable(false);
    }

    @Override
    public void postSwap(){
        super.postSwap();

        BufferedImage bufferedImage = new BufferedImage(700, 700, BufferedImage.TYPE_4BYTE_ABGR);

        ImageComponent2D rb = new ImageComponent2D(ImageComponent.FORMAT_RGBA,bufferedImage);
        DepthComponentInt dc = new DepthComponentInt(700,700);
        Raster raster = new Raster(new Point3f(-1.0f,-1.0f,-1.0f),
                Raster.RASTER_COLOR_DEPTH,
                0,0,
                700,700,rb,dc);

        this.setOffScreenBuffer(rb);
        //this.renderOffScreenBuffer();
        //this.waitForOffScreenRendering();
        this.getGraphicsContext3D().readRaster(raster);
        bufferedImage = raster.getImage().getImage();
        bufferedImage.createGraphics().drawImage(bufferedImage,0,0,null);
        try {
            ImageIO.write(bufferedImage, "png", new File("mask.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
