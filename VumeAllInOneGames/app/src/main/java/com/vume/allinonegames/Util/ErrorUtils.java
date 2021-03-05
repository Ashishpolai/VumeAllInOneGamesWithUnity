package com.vume.allinonegames.Util;

import com.google.gson.Gson;
import com.vume.allinonegames.Model.JoshErrorResponse;

import retrofit2.Response;

public class ErrorUtils {

    public static final String MISSING_KYC_ERRORCODE = "err-missing-kyc";

    public static JoshErrorResponse parseError(Response<?> response) {
        Gson gson = new Gson();
        try {
            JoshErrorResponse errorResponseObj = gson.fromJson(response.errorBody().charStream(), JoshErrorResponse.class);
            return errorResponseObj;
        }
        catch (Exception e){
            return  null;
        }
    }
}