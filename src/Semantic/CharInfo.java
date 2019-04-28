package Semantic;

public class CharInfo {

	private String name;
	private String type;
	private int width;
	private int offset;
	private String addr;
	
	public CharInfo(String name, String type, int width, int offset) {
		this.name = name;
		this.type = type;
		this.width = width;
		this.offset = offset;
		this.addr = "~";
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	public String getAddr() {
		return addr;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getOffset() {
		return offset;
	}
	
	@Override
	public String toString() {
		return "name = " + name + "\ttype = " + type + "\toffset = " + offset + "\taddr = " + addr;
	}
	
}
