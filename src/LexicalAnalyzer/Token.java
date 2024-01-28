package LexicalAnalyzer;
/**
*
* @Description COMP442 A1
* @author Yizhou Sun 40056775
* @date Jan 28, 2024
*/
public class Token {
	private TokenName name;
    private String value;
    private int lineNum;

    public Token(TokenName name, String value, int lineNum){
        this.name = name;
        this.value = value;
        this.lineNum = lineNum;
    }

    public TokenName getName(){
        return this.name;
    }

    public String getValue(){
        return this.value;
    }

    public int getLineNum(){
        return this.lineNum;
    }

    @Override
    public String toString(){
        return "[" + this.name + ", " + this.value + ", " + this.lineNum + "]";
    }
}
