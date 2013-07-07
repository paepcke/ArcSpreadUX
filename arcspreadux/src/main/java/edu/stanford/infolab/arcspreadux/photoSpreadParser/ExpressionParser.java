/* Generated By:JavaCC: Do not edit this line. ExpressionParser.java */
package edu.stanford.infolab.arcspreadux.photoSpreadParser;
import java.io.StringReader;

import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadCellRange;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadCellRangeCondition;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadCondition;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadConstant;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadConstantExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadContainerExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadDoubleConstant;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadFormulaExpression;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadSimpleCondition;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadSpecialConstants;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadSpecialConstants.PhotoSpreadNullConstant;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.PhotoSpreadStringConstant;
import edu.stanford.infolab.arcspreadux.photoSpreadParser.photoSpreadExpression.photoSpreadFunctions.PhotoSpreadFunction;
import edu.stanford.infolab.arcspreadux.photoSpreadTable.PhotoSpreadCell;
import edu.stanford.infolab.arcspreadux.photoSpreadUtilities.Const;

// ***** end Real imports ****/
/**** Imports within test environment
import photoSpreadParser.parseTestSkeleton.PhotoSpreadCell;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadCellRange;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadCellRangeCondition;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadCondition;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadConstantExpression;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadDoubleConstant;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadStringConstant;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadContainerExpression;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadExpression;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadFormulaExpression;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadFunction;
import photoSpreadParser.parseTestSkeleton.photoSpreadObjects.PhotoSpreadConstant;
import photoSpreadParser.parseTestSkeleton.PhotoSpreadSimpleCondition;
import photoSpreadParser.parseTestSkeleton.photoSpreadObjects.PhotoSpreadDoubleObject;
import photoSpreadParser.parseTestSkeleton.photoSpreadObjects.PhotoSpreadObject;
import photoSpreadParser.parseTestSkeleton.photoSpreadObjects.PhotoSpreadStringObject;
//End Imports within test environment */
public class ExpressionParser implements ExpressionParserConstants {
  private static final boolean GET_FROM_START = true;

  private PhotoSpreadCell _cell;
  private String theStrToParse;
  // Keep track of whether we have ascertained
  // that the formula string is a formula (starts
  // with '=':
  private boolean isFormula = false;

  public ExpressionParser(PhotoSpreadCell _cell, String strToParse)
  {
    this(new StringReader(strToParse));
    this._cell = _cell;
    theStrToParse = strToParse;
  }

  private String grabAllRest(boolean fromStart) {
    if (fromStart)
      return theStrToParse;
    // Return not the entire input string,
    // but only what is left unparsed so far:
    String extraText = "";
    Token eofToken;
        while ((eofToken = getNextToken()).kind != EOF) extraText += eofToken.image + " ";
        return extraText;
  }

  public static void main(String args []) throws ParseException
  {
    java.io.StringReader sr = new java.io.StringReader("=A1[hello = jello]");
    java.io.Reader r = new java.io.BufferedReader(sr);
    ExpressionParser parser = new ExpressionParser(r);
    PhotoSpreadExpression expr = parser.Expression();
    System.out.println(expr.toString());
  }

  final public PhotoSpreadExpression Expression() throws ParseException {
  PhotoSpreadExpression e;
  PhotoSpreadConstant nonFormulaConst;
  PhotoSpreadConstantExpression expr;
  Token aToken;
  String extraText = "";
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQ:
        jj_consume_token(EQ);
        e = FormulaExpression();
    // Must not have any left-over tokens after the expression:
    extraText = "";
    //while ((eofToken = getNextToken()).kind != EOF) extraText += eofToken.image;
    extraText += grabAllRest(!GET_FROM_START);
    if (!extraText.isEmpty()) {if (true) throw new ParseException("Can't make sense of final formula portion: '"+ extraText+ "'");}
    return e;
	case NUMBER:
      case SIGNED_NUMBER:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case SIGNED_NUMBER:
          aToken = jj_consume_token(SIGNED_NUMBER);
          break;
        case NUMBER:
          aToken = jj_consume_token(NUMBER);
          break;
        default:
          jj_la1[0] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
    nonFormulaConst = new PhotoSpreadDoubleConstant(this._cell, aToken.image);
    // Error if any text comes after the number:
    extraText = grabAllRest(!GET_FROM_START);
    if (!extraText.isEmpty()) {
      {if (true) throw new ParseException(
        "Non-formula text must be a sole number, or text that does not start with a number: '"+
        extraText + "'");}
        } else {
          expr = new PhotoSpreadConstantExpression();
      expr.addConstant(nonFormulaConst);
      return expr;
    }
	case SPECIAL_CONST:
        aToken = jj_consume_token(SPECIAL_CONST);
        expr = SpecialConstant();
        return expr;
	default:
        jj_la1[1] = jj_gen;
    // Non-formula, nor a number or special object. The whole
    // entry in the formula editor is a string with no special
    // meaning (See also comment in catch clause below):
    nonFormulaConst = new PhotoSpreadStringConstant(this._cell, theStrToParse);
    expr = new PhotoSpreadConstantExpression();
    expr.addConstant(nonFormulaConst);
    return expr;
      }
    } catch (TokenMgrError ex) {
        // For some non-formula strings in the formula editor
        // even the check for a leading '=' sign throws
        // an error. E.g. when the first char is a '!',
        // which is part of the grammar. We therefore
        // catch all token parsing exceptions. If we
        // are in fact parsing a formula (formula string
        // starts with '=' then we re-throw the exception.
        // Else we accept the entire string and return
        // a string constant.
        if (isFormula)
        {
          {if (true) throw ex;}
        }
    nonFormulaConst = new PhotoSpreadStringConstant(this._cell, theStrToParse);
    expr = new PhotoSpreadConstantExpression();
    expr.addConstant(nonFormulaConst);
    return expr;
    }
  }

  final public PhotoSpreadFormulaExpression FormulaExpression() throws ParseException {
  PhotoSpreadFormulaExpression fe;
  Token selection;
  PhotoSpreadCondition condition;

  // At this point we are sure that the
  // string in the formula editor starts
  // with '='. 
  isFormula = true;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DOLLAR:
    case UNION:
    case SUM:
    case MIN:
    case MAX:
    case COUNT:
    case AVG:
    case CHARS_UPPER:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOLLAR:
      case CHARS_UPPER:
        fe = ContainerExpression();

        break;
      case UNION:
      case SUM:
      case MIN:
      case MAX:
      case COUNT:
      case AVG:
        fe = FunctionCall();

        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LBRACKET:
        jj_consume_token(LBRACKET);
        condition = Condition();
          fe.addCondition(condition, "NO_BOOLEAN");
        label_1:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case AND:
          case OR:
            ;
            break;
          default:
            jj_la1[3] = jj_gen;
            break label_1;
          }
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case OR:
            jj_consume_token(OR);
            condition = Condition();
            fe.addCondition(condition, "OR");
            break;
          case AND:
            jj_consume_token(AND);
            condition = Condition();
            fe.addCondition(condition, "AND");
            break;
          default:
            jj_la1[4] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
        jj_consume_token(RBRACKET);
        break;
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PERIOD:
        jj_consume_token(PERIOD);
        selection = jj_consume_token(IDENTIFIER);
          fe.addSelection(selection.image);
        break;
      default:
        jj_la1[6] = jj_gen;
        ;
      }
      break;
    case SPECIAL_CONST:
    case NUMBER:
    case IDENTIFIER:
    case STRING_CONSTANT:
      fe = Constant();
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    return fe;
  }

  final public PhotoSpreadContainerExpression ContainerExpression() throws ParseException {
  PhotoSpreadContainerExpression ce;
  PhotoSpreadCellRange cellRange;
    cellRange = OldCellRange();
    ce = new PhotoSpreadContainerExpression(cellRange);
    return ce;
  }

  final public PhotoSpreadConstantExpression ConstantExpression() throws ParseException {
  PhotoSpreadConstant obj;
  PhotoSpreadConstantExpression expr = new PhotoSpreadConstantExpression();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SPECIAL_CONST:
    case NUMBER:
    case IDENTIFIER:
    case STRING_CONSTANT:
      obj = Constant();
      expr = new PhotoSpreadConstantExpression();
      expr.addConstant(obj);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        obj = Constant();
        expr = new PhotoSpreadConstantExpression();
        expr.addConstant(obj);
      }
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
    return expr;
  }

  final public PhotoSpreadConstant Constant() throws ParseException {
  Token str;
  PhotoSpreadConstant obj;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SPECIAL_CONST:
      str = jj_consume_token(SPECIAL_CONST);
        obj = SpecialConstant();
      break;
    case NUMBER:
      str = jj_consume_token(NUMBER);
        obj = new PhotoSpreadDoubleConstant(this._cell, str.image);
      break;
    case IDENTIFIER:
      str = jj_consume_token(IDENTIFIER);
            obj = new PhotoSpreadStringConstant(this._cell, str.image);
      break;
    case STRING_CONSTANT:
      str = jj_consume_token(STRING_CONSTANT);
        obj = new PhotoSpreadStringConstant(this._cell, str.image);
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    return obj;
  }

  final public PhotoSpreadConstant SpecialConstant() throws ParseException {
  Token specConst;
  PhotoSpreadConstant obj;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case OBJ_COLLECTION:
      specConst = jj_consume_token(OBJ_COLLECTION);
        obj = new PhotoSpreadSpecialConstants.ObjectsCollectionConstant(this._cell);
      break;
    case NULL:
      specConst = jj_consume_token(NULL);
            obj = PhotoSpreadSpecialConstants.PhotoSpreadNullConstant.getInstance();
      break;
    default:
      jj_la1[11] = jj_gen;
      {if (true) throw new ParseException("Unknown special constant: '"+ getToken(0)+ "'");}
    }
    {if (true) return obj;}
    throw new Error("Missing return statement in function");
  }

  final public PhotoSpreadFunction FunctionCall() throws ParseException {
  PhotoSpreadFunction func;
  String functionName;
  PhotoSpreadFormulaExpression fe;
    functionName = FunctionName();
    func = PhotoSpreadFunction.getInstance(functionName.toLowerCase(), this._cell);
    jj_consume_token(LPARENS);

    fe = FormulaExpression();
    func.addArgument(fe);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[12] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      fe = FormulaExpression();
      func.addArgument(fe);
    }
    jj_consume_token(RPARENS);
    {if (true) return func;}
    throw new Error("Missing return statement in function");
  }

  final public PhotoSpreadCellRange OldCellRange() throws ParseException {
  Token startCol;
  boolean startColFixed = false;
  Token startRow;
  boolean startRowFixed = false;
  Token endCol;
  boolean endColFixed = false;
  Token endRow;
  boolean endRowFixed = false;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DOLLAR:
      jj_consume_token(DOLLAR);
      startColFixed = true;
      break;
    default:
      jj_la1[13] = jj_gen;
      ;
    }
    startCol = jj_consume_token(CHARS_UPPER);

    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DOLLAR:
      jj_consume_token(DOLLAR);
      startRowFixed = true;
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
    startRow = jj_consume_token(NUMBER);


    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 50:
      jj_consume_token(50);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOLLAR:
        jj_consume_token(DOLLAR);
      endColFixed = true;
        break;
      default:
        jj_la1[15] = jj_gen;
        ;
      }
      endCol = jj_consume_token(CHARS_UPPER);

      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOLLAR:
        jj_consume_token(DOLLAR);
      endRowFixed = true;
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      endRow = jj_consume_token(NUMBER);

      {if (true) return new PhotoSpreadCellRange(startColFixed, startCol.image, startRowFixed, Integer.parseInt(startRow.image), endColFixed, endCol.image, endRowFixed, Integer.parseInt(endRow.image));}
      break;
    default:
      jj_la1[17] = jj_gen;
      ;
    }
    {if (true) return new PhotoSpreadCellRange(startColFixed, startCol.image, startRowFixed, Integer.parseInt(startRow.image));}
    throw new Error("Missing return statement in function");
  }

  final public PhotoSpreadCondition Condition() throws ParseException {
  PhotoSpreadCondition cond;
  Token rhsToken;
  Token lhsToken;
  Token compOpToken;
  String rhs;
  String compOp;
  String lhs;
  PhotoSpreadCellRange cellRange;
    rhsToken = jj_consume_token(IDENTIFIER);
    rhs = rhsToken.image;
    compOpToken = CompOp();
    compOp = compOpToken.image;
    if (jj_2_1(2)) {
      cellRange = OldCellRange();
        cond = new PhotoSpreadCellRangeCondition(rhs, compOp, cellRange);
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NULL:
        lhsToken = jj_consume_token(NULL);
            lhs = Const.NULL_VALUE_STRING; cond = new PhotoSpreadSimpleCondition(rhs, compOp, PhotoSpreadNullConstant.getInstance());
        break;
      case NUMBER:
        lhsToken = jj_consume_token(NUMBER);
        lhs = lhsToken.image; cond = new PhotoSpreadSimpleCondition(rhs, compOp, lhs);
        break;
      case IDENTIFIER:
        lhsToken = jj_consume_token(IDENTIFIER);
        lhs = lhsToken.image; cond = new PhotoSpreadSimpleCondition(rhs, compOp, lhs);
        break;
      case STRING_CONSTANT:
        lhsToken = jj_consume_token(STRING_CONSTANT);
        lhs = lhsToken.image; cond = new PhotoSpreadSimpleCondition(rhs, compOp, lhs);
        break;
      default:
        jj_la1[18] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    {if (true) return cond;}
    throw new Error("Missing return statement in function");
  }

  final public Token CompOp() throws ParseException {
  Token comp;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQ:
      comp = jj_consume_token(EQ);

      break;
    case NE:
      comp = jj_consume_token(NE);

      break;
    case LEQ:
      comp = jj_consume_token(LEQ);

      break;
    case GEQ:
      comp = jj_consume_token(GEQ);

      break;
    case LT:
      comp = jj_consume_token(LT);

      break;
    case GT:
      comp = jj_consume_token(GT);

      break;
    default:
      jj_la1[19] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return comp;}
    throw new Error("Missing return statement in function");
  }

  final public String FunctionName() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case UNION:
      jj_consume_token(UNION);
    {if (true) return "union";}
      break;
    case SUM:
      jj_consume_token(SUM);
    {if (true) return "sum";}
      break;
    case MIN:
      jj_consume_token(MIN);
    {if (true) return "min";}
      break;
    case MAX:
      jj_consume_token(MAX);
    {if (true) return "max";}
      break;
    case AVG:
      jj_consume_token(AVG);
    {if (true) return "avg";}
      break;
    case COUNT:
      jj_consume_token(COUNT);
    {if (true) return "count";}
      break;
    default:
      jj_la1[20] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public int MatchedBraces() throws ParseException {
  int nested_count = 0;
    jj_consume_token(LBRACE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LBRACE:
      nested_count = MatchedBraces();
      break;
    default:
      jj_la1[21] = jj_gen;
      ;
    }
    jj_consume_token(RBRACE);
    {if (true) return++ nested_count;}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(DOLLAR)) return true;
    return false;
  }

  private boolean jj_3R_5() {
    if (jj_scan_token(DOLLAR)) return true;
    return false;
  }

  private boolean jj_3R_4() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_5()) jj_scanpos = xsp;
    if (jj_scan_token(CHARS_UPPER)) return true;
    xsp = jj_scanpos;
    if (jj_3R_6()) jj_scanpos = xsp;
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_4()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ExpressionParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[22];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x0,0x100020,0xfc010000,0xc000,0xc000,0x200,0x800000,0xfc010020,0x2000,0x20,0x20,0x40,0x2000,0x10000,0x10000,0x10000,0x10000,0x0,0x40,0x7e0000,0xfc000000,0x80,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x3,0x3,0x80,0x0,0x0,0x0,0x0,0x8181,0x0,0x8101,0x8101,0x20000,0x0,0x0,0x0,0x0,0x0,0x40000,0x8101,0x0,0x0,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public ExpressionParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ExpressionParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public ExpressionParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ExpressionParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public ExpressionParser(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ExpressionParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[51];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 22; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 51; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
