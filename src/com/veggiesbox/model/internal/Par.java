package com.veggiesbox.model.internal;


public class Par<T,S> {
	
	public T first;
	public S second;
	   
	public Par( T a, S b ) {  // Constructor.
	      first = a;
	      second = b;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

}
