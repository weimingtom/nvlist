package nl.weeaboo.vn.impl.base;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.IDrawBuffer;
import nl.weeaboo.vn.IGeometryShader;
import nl.weeaboo.vn.IImageDrawable;
import nl.weeaboo.vn.IImageTween;
import nl.weeaboo.vn.IInput;
import nl.weeaboo.vn.ILayer;
import nl.weeaboo.vn.ITexture;
import nl.weeaboo.vn.layout.LayoutUtil;
import nl.weeaboo.vn.math.Vec2;

public abstract class BaseImageDrawable extends BaseTransformable implements IImageDrawable {

	private static final long serialVersionUID = BaseImpl.serialVersionUID;
	
	private IImageTween tween;
	private ITexture image;
	private IGeometryShader geometryShader;
	private Area2D uv;
	
	public BaseImageDrawable() {
		uv = IDrawBuffer.DEFAULT_UV;
	}
	
	//Functions
	@Override
	public void destroy() {
		super.destroy();
		
		//Allow some memory to be freed
		tween = null;
		image = null;
		geometryShader = null;
		updateUnscaledSize();
	}
	
	@Override
	public boolean update(ILayer layer, IInput input, double effectSpeed) {
		if (super.update(layer, input, effectSpeed)) {
			markChanged();
		}
		
		if (geometryShader != null) {
			if (geometryShader.update(effectSpeed)) {
				markChanged();
			}
		}
		
		if (tween != null) {
			if (tween.update(effectSpeed)) {
				markChanged();
			}
		}
		
		return consumeChanged();
	}
	
	@Override
	public void draw(IDrawBuffer d) {
		if (tween != null && tween.isPrepared() && !tween.isFinished()) {
			tween.draw(d);
		} else {
			d.draw(this);
		}
	}
	
	protected void finishTween() {
		if (tween != null) {
			if (!tween.isFinished()) {
				tween.finish();
				markChanged();
			}
			tween = null;
			updateUnscaledSize();
		}		
	}
	
	private void updateUnscaledSize() {
		if (tween != null) {
			setUnscaledSize(tween.getWidth(), tween.getHeight());
		} else if (image != null) {			
			setUnscaledSize(image.getWidth(), image.getHeight());
		} else {
			setUnscaledSize(0, 0);
		}
	}
		
	//Getters
	@Override
	public ITexture getTexture() {
		if (tween != null) return tween.getEndTexture();

		return image;
	}
		
	@Override
	public IGeometryShader getGeometryShader() {
		return geometryShader;
	}
		
	@Override
	public Area2D getUV() {
		return uv;
	}
	
	//Setters	
	@Override
	public void setTexture(ITexture i) {
		setTexture(i, 7);
	}
	
	@Override
	public void setTexture(ITexture i, int anchor) {
		double alignX = 0, alignY = 0;
		if (image != i) {
			double w0 = getUnscaledWidth();
			double h0 = getUnscaledHeight();
			double w1 = (i != null ? i.getWidth() : 0);
			double h1 = (i != null ? i.getHeight() : 0);
			Rect2D rect = LayoutUtil.getBounds(w0, h0, getAlignX(), getAlignY());			
			Vec2 align = LayoutUtil.alignSubRect(rect, w1, h1, anchor);
			alignX = align.x;
			alignY = align.y;
		}
		
		setTexture(i, alignX, alignY);
	}
	
	@Override
	public void setTexture(ITexture i, double imageAlignX, double imageAlignY) {
		finishTween();
		
		if (image != i || getAlignX() != imageAlignX || getAlignY() != imageAlignY) {			
			image = i;
			
			markChanged();
			updateUnscaledSize();
			invalidateCollisionShape();			
			setAlign(imageAlignX, imageAlignY);
		}
	}
	
	@Override
	public void setTween(IImageTween t) {
		finishTween();
		
		if (tween != t) {
			t.setStartImage(this);

			tween = t;			
			if (!tween.isPrepared()) {
				tween.prepare();
			}
			
			markChanged();
			updateUnscaledSize();			
		}
	}
	
	@Override
	public void setGeometryShader(IGeometryShader gs) {
		if (geometryShader != gs) {
			geometryShader = gs;
			
			markChanged();
		}
	}
	
	@Override
	public final void setUV(double w, double h) {
		setUV(0, 0, w, h);
	}
	
	@Override
	public final void setUV(double x, double y, double w, double h) {
		setUV(new Area2D(x, y, w, h));
	}
	
	@Override
	public void setUV(Area2D uv) {
		if (!uv.equals(this.uv)) {
			this.uv = uv;
			
			markChanged();
		}
	}
			
}
