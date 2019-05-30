package ensharp.yeey.whisperer.Common.VO;

public class AnalyzeVO {

    String Label;
    String Value;
    String Input;
    String Text;

    public String getLabel() { return Label; }

    public void setLabel(String label) { Label = label; }

    public String getValue() { return Value; }

    public void setValue(String value) { Value = value; }

    public String getInput() { return Input; }

    public void setInput(String input) { Input = input; }

    public String getText() { return Text; }

    public void setText(String text) { Text = text; }

    @Override
    public String toString() {
        return "Analyze Result [label:" + Label
                + ", Value:" + Value
                + ", Input:" + Input
                + ", Text:" + Text
                + "]";
    }
}
