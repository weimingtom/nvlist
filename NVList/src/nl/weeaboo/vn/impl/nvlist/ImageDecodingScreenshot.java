package nl.weeaboo.vn.impl.nvlist;

import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import nl.weeaboo.awt.ImageUtil;
import nl.weeaboo.common.Dim;
import nl.weeaboo.io.ByteBufferInputStream;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.vn.impl.base.DecodingScreenshot;

@LuaSerializable
class ImageDecodingScreenshot extends DecodingScreenshot {
	
	private static final long serialVersionUID = 2L;
	
	private boolean preciseScaling = true;
	
	private final Dim targetSize; //May be null
	
	public ImageDecodingScreenshot(ByteBuffer b, Dim targetSize) {
		super(b);
		
		this.targetSize = targetSize;
	}
	
	@Override
	protected void tryLoad(ByteBuffer data) {
		BufferedImage image = null;
		if (data != null) {
			ImageInputStream iin = null;
			try {
				iin = ImageIO.createImageInputStream(new ByteBufferInputStream(data));
				Iterator<ImageReader> itr = ImageIO.getImageReaders(iin);
				while (image == null && itr.hasNext()) {
					ImageReader reader = itr.next();
					reader.setInput(iin);
					int w = reader.getWidth(0);
					int h = reader.getHeight(0);
					
					ImageReadParam readParam = reader.getDefaultReadParam();
					if (targetSize != null && !preciseScaling) {
						int sampleFactor = Math.max(1, Math.min(w/targetSize.w, h/targetSize.h));
						readParam.setSourceSubsampling(sampleFactor, sampleFactor, sampleFactor>>1, sampleFactor>>1);
					}

					image = reader.read(0, readParam);
					if (image != null && targetSize != null && preciseScaling) {
						image = ImageUtil.getScaledImageProp(image, targetSize.w, targetSize.h, Transparency.OPAQUE);
					}
				}
			} catch (IOException ioe) {
				//Ignore
			} finally {
				try {
					if (iin != null) iin.close();
				} catch (IOException ioe) {
					//Ignore
				}
			}
		}
		
		if (image == null) {
			cancel();
		} else {
			int w = image.getWidth();
			int h = image.getHeight();
			int argb[] = new int[w * h];
			ImageUtil.getPixels(image, IntBuffer.wrap(argb), 0, w);			
			setPixels(argb, w, h, w, h);
		}
	}
	
}