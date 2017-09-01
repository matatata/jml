package fl.frontend.ast2;

import fl.core.ILst;
import fl.core.Lists;
import fl.core.LstConstructor;
import fl.core.TupelConstructor;
import fl.core.Tupels;
import fl.core.Unit;
import fl.eval.Env;
import fl.eval.types.DataType;
import fl.eval.types.DataTypeInstance;
import fl.eval.types.IDataTypeInstance;
import fl.eval.types.ITypeConstructor;
import fl.eval.types.TypeConstructor;
import fl.frontend.ast.AstType;
import fl.frontend.ast.Symbol;
import fl.frontend.tast.TypeSymbol;

public class AstConstructor extends TypeConstructor {
//	new AstConstructor(AstType.FN),
//	new AstConstructor(AstType.LET),
//	new AstConstructor(AstType.SEQ,LstConstructor.cons?),
//	new AstConstructor(AstType.TYPE)
	public static DataType astDataType = new DataType("ast"){
		protected boolean isCompatibleDataType(DataType actualDataType) {
			if(actualDataType == TupelConstructor.tupelT || actualDataType==LstConstructor.listT)
				return true;
			return super.isCompatibleDataType(actualDataType);
		}
	};

	public static final AstConstructor NUM = new AstConstructor(AstType.NUM, new TypeSymbol(Number.class));
	public static final AstConstructor STRING = new AstConstructor(AstType.STRING, new TypeSymbol(String.class));
	public static final AstConstructor BOOL = new AstConstructor(AstType.BOOL, new TypeSymbol(Boolean.class));
//	new AstConstructor(AstType.DATATYPE,LstConstructor.listT),
//	new AstConstructor(AstType.TAN,Tupels.of(elems)),
//	new AstConstructor(AstType.REC),
//	new AstConstructor(AstType.VAL),
	
	public static final AstConstructor IF = new AstConstructor(AstType.IF,Tupels.of(astDataType,astDataType,astDataType));
	
	public static final AstConstructor RULE = new AstConstructor(AstType.RULE, Tupels.of(astDataType, astDataType));
	
	//list of RULE
	public static final AstConstructor FN = new AstConstructor(AstType.FN,LstConstructor.listT);
	
	
	public static final AstConstructor APP = new AstConstructor(AstType.APP,Tupels.of(astDataType,astDataType));
	public static final AstConstructor SYMBOL = new AstConstructor(AstType.SYMBOL,new TypeSymbol(String.class)){
//		protected DataTypeInstance createDataTypeInstance(Object x) {
//			return new Symbol((String) x);
//		};
	};
	
	public static final TypeConstructor UNIT = new TypeConstructor(astDataType,"UNIT",Unit.UNIT){
		@Override
		protected IDataTypeInstance createDataTypeInstance(Object x) {
			return Unit.UNIT;
		}
	};
		
	
	private AstType type;
	
	public AstConstructor(AstType type,Object texp) {
		super(astDataType,type.name(),texp);
		this.type = type;
	}
	
	
	
	public static class AstNode extends DataTypeInstance implements IAstNode{
		public AstNode(AstConstructor constructor, Object data) {
			super(constructor, data);
		}
	}
	
	public static class Application extends AstNode{
		public Application(Object data) {
			super(APP, data);
		}
	}
	
	
	@Override
	protected DataTypeInstance createDataTypeInstance(Object x) {
		switch (type) {
		case APP:
			return new Application(x);
		case SYMBOL:
			return new Symbol(x.toString());
		default:
			break;
		}
		return new AstNode(this,x);
	}
	
	public static void install(Env env){
		ILst ctors = Lists.of(
				IF,
				FN,
				RULE,
				APP,
				NUM,
				STRING,
				BOOL,
				UNIT,
				SYMBOL,
				LstConstructor.cons
				);
		
		astDataType.setConstructors(ctors);
		
		env.addTypeDef(astDataType.getName(), astDataType);
		for(Object c : ctors ){
			env.define(((ITypeConstructor)c).getName(), c);
		}
	}
	
	
	
	
	
	
}
