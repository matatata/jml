package fl.frontend.ast;

import fl.frontend.ast2.AstConstructor;
import fl.frontend.ast2.AstConstructor.AstNode;

public class Symbol 
extends AstNode 
{
	public final String image;
	
	public Symbol(String image) {
		super(AstConstructor.SYMBOL,image);
		this.image=image;
	}
	
	@Override
	public String toString() {
		return image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symbol other = (Symbol) obj;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		return true;
	}
	
	
}
