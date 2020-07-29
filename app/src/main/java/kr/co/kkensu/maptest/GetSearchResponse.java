package kr.co.kkensu.maptest;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by johyunchol on 2017. 11. 25..
 */

public class GetSearchResponse implements Serializable {
    @SerializedName("code")
    protected int code;
    @SerializedName("message")
    protected String message;
    @SerializedName("data")
    protected Response data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Response getData() {
        return data;
    }

    public void setData(Response data) {
        this.data = data;
    }
}
