package architecture.community.web.model;

import java.util.HashMap;

import architecture.community.util.SecurityHelper;

public class Result implements java.io.Serializable {
	
	private Error error;
	private boolean anonymous;
	private boolean success;
	private Integer count = 0;
	private HashMap<String, Object> data;

	public Result() {
		data = new HashMap<String, Object>();
		error = null;
		success = true;		
	}
	
	public Result(String name, Object value) {
		data = new HashMap<String, Object>();
		data.put(name, value);
		error = null;
		success = true;
	}
	

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	
	public void setError(Throwable e) {
		this.error = new Error(e);
		this.success = false;
		this.count = 0;
	}

	public HashMap<String, Object> getData() {
		return data;
	}
	
	public static Result newResult(String name, Object value) {
		Result r = new Result(name, value);
		r.anonymous = SecurityHelper.getUser().isAnonymous();
		return r;
	}
	

	public static Result newResult() {
		Result r = new Result();
		r.anonymous = SecurityHelper.getUser().isAnonymous();
		return r;
	}

	public static Result newResult(Throwable e) {
		Result r = new Result();
		r.setError(e);
		r.anonymous = SecurityHelper.getUser().isAnonymous();
		return r;
	}

	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
