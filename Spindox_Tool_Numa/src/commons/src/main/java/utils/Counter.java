package commons.src.main.java.utils;

public class Counter {
	int x = 0;
	boolean print = false;
	boolean bool1 = false;
	
	public void print() {
		System.out.println(this.x++);
	}
	
	public int getX() {
		return x;
	}
	
	public void shouldPrint() {
		this.print = true;
	}
	
	public void nowDoIt() {
		bool1 = true;
	}
	
	public boolean isPrint() {
		return print;
	}
	
	public boolean isBool1() {
		return bool1;
	}
}
