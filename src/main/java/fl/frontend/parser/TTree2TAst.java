package fl.frontend.parser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;

import fl.core.Records;
import fl.core.Tupels;
import fl.core.Unit;
import fl.frontend.tast.Arrow;
import fl.frontend.tast.TypeSymbol;
import fl.frontend.tast.TypeVariable;

public class TTree2TAst extends TTreeVisitor {

	public TTree2TAst() {
	}

	@Override
	protected Object visitSymbol(String text) {
		return new TypeSymbol(text);
	}

	@Override
	protected Object visitTypeVariable(String text) {
		return new TypeVariable(text);
	}

	@Override
	protected Object visitProduct(CommonTree t) {
		if(t.getChildCount()==0)
			return Unit.UNIT;
		return Tupels.of(visitChilds(t));
	}

	@Override
	protected Object visitTFn(CommonTree t) {
		return new Arrow(visit(t.getChild(0)), visit(t.getChild(1)));
	}

	@Override
	protected Object visitTRec(CommonTree t) {
		Map<String,Object> map=new HashMap<String,Object>();
		for(CommonTree e : (Collection<CommonTree>)t.getChildren()){
			String key=e.getChild(0).getText();
			Object val=visit(e.getChild(1));
			map.put(key, val);
		}
		return Records.recordOf(map);
	}


}
