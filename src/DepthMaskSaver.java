import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.imageio.ImageIO;
import javax.media.j3d.BranchGroup;
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

public class DepthMaskSaver {

    public static void main(String[] args) {
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        /*this one works - it saves the picture of one side of the cube to image.png*/
        //saveImage(getUniverse(new Canvas3D(config,true)));

        /*this one gets into the infinite loop at GraphicsContext3D.java:2242 - while (!readRasterReady) loops forever*/
        saveDepthMask(getUniverse(new Canvas3D(config,true)));

        /*this one saves image.png as in first example, but throws exception on postSwap() not saving the depth mask*/
        //saveImage(getUniverse(new MyCanvas3D(config,true)));

    }

    static SimpleUniverse getUniverse(Canvas3D c) {
        // = new Canvas3D(config,true);
        c.getScreen3D().setSize(350, 350);
        c.getScreen3D().setPhysicalScreenWidth(0.254 / 90.0 * 400);
        c.getScreen3D().setPhysicalScreenHeight(0.254 / 90.0 * 400);

        SimpleUniverse u = new SimpleUniverse(c);

        u.getViewingPlatform().setNominalViewingTransform();

        u.addBranchGraph(new BranchGroup(){{
            addChild(new ColorCube(0.4));
        }});

        return u;
    }

    static void saveImage(SimpleUniverse u){
        BufferedImage bufferedImage = new BufferedImage(700, 700, BufferedImage.TYPE_4BYTE_ABGR);
        ImageComponent2D buffer = new ImageComponent2D(ImageComponent.FORMAT_RGBA, bufferedImage);
        u.getCanvas().setOffScreenBuffer(buffer);
        u.getCanvas().renderOffScreenBuffer();
        u.getCanvas().waitForOffScreenRendering();

        bufferedImage = u.getCanvas().getOffScreenBuffer().getImage();

        BufferedImage image = new BufferedImage(700, 700, BufferedImage.TYPE_4BYTE_ABGR);
        image.createGraphics().drawImage(bufferedImage,0,0,null);

        try {
            ImageIO.write(image, "png", new File("image.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void saveDepthMask(SimpleUniverse u){
        BufferedImage bufferedImage = new BufferedImage(700, 700, BufferedImage.TYPE_4BYTE_ABGR);

        ImageComponent2D rb = new ImageComponent2D(ImageComponent.FORMAT_RGBA,bufferedImage);
        DepthComponentInt dc = new DepthComponentInt(700,700);
        Raster raster = new Raster(new Point3f(-1.0f,-1.0f,-1.0f),
                Raster.RASTER_COLOR_DEPTH,
                0,0,
                700,700,rb,dc);

        u.getCanvas().setOffScreenBuffer(rb);
        u.getCanvas().renderOffScreenBuffer();
        u.getCanvas().waitForOffScreenRendering();
        u.getCanvas().getGraphicsContext3D().readRaster(raster);
        bufferedImage = raster.getImage().getImage();
        bufferedImage.createGraphics().drawImage(bufferedImage,0,0,null);
        try {
            ImageIO.write(bufferedImage, "png", new File("mask.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
