package nl.weeaboo.vn.impl.base;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.vn.BlendMode;
import nl.weeaboo.vn.IDrawBuffer;
import nl.weeaboo.vn.IInterpolator;
import nl.weeaboo.vn.IPixelShader;
import nl.weeaboo.vn.layout.LayoutUtil;
import nl.weeaboo.vn.math.Matrix;

@LuaSerializable
public class CrossFadeTween extends BaseImageTween {
		
	private static final long serialVersionUID = BaseImpl.serialVersionUID;

	private final IInterpolator interpolator;
	
	public CrossFadeTween(double duration, IInterpolator i) {
		super(duration);
		
		interpolator = (i != null ? i : Interpolators.LINEAR);
	}
	
	//Functions	
	@Override
	public void draw(IDrawBuffer d) {
		short z = drawable.getZ();
		boolean clip = drawable.isClipEnabled();
		BlendMode blend = drawable.getBlendMode();
		int argb = drawable.getColorARGB();
		Matrix trans = drawable.getTransform();
		IPixelShader ps = drawable.getPixelShader();
		Area2D uv = drawable.getUV();
		double frac = interpolator.remap((float)getNormalizedTime());
		
		if (frac <= 0 || getEndTexture() == null) {
			Rect2D bounds = LayoutUtil.getBounds(getStartTexture(), getStartAlignX(), getStartAlignY());
			d.drawQuad(z, clip, blend, argb, getStartTexture(), trans,
					bounds.toArea2D(), uv, ps);
		} else if (frac >= 1 || getStartTexture() == null) {
			Rect2D bounds = LayoutUtil.getBounds(getEndTexture(), getEndAlignX(), getEndAlignY());
			d.drawQuad(z, clip, blend, argb, getEndTexture(), trans,
					bounds.toArea2D(), uv, ps);
		} else {
			d.drawBlendQuad(z, clip, blend, argb,
					getStartTexture(), getStartAlignX(), getStartAlignY(),
					getEndTexture(), getEndAlignX(), getEndAlignY(),
					trans, uv, ps,
					frac);
		}
	}
	
	//Getters
	
	//Setters
	
}
