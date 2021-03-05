package com.vume.allinonegames.ApiCalls;

import com.vume.allinonegames.Model.FantasyAllMatchesResponseData;
import com.vume.allinonegames.Model.FantasyContestDetailsWithMatchAndPlayerData;
import com.vume.allinonegames.Model.FantasyContestsForMatchResponseData;
import com.vume.allinonegames.Model.FantasyCreateTeamRequestData;
import com.vume.allinonegames.Model.FantasyCreateTeamResponseData;
import com.vume.allinonegames.Model.FantasyGetMatchDetailsResponseData;
import com.vume.allinonegames.Model.FantasyGetMyTeamsResponseData;
import com.vume.allinonegames.Model.FantasyJoinContestRequestData;
import com.vume.allinonegames.Model.FantasyJoinContestResponseData;
import com.vume.allinonegames.Model.JoshAllTransactionsResponseData;
import com.vume.allinonegames.Model.JoshBankAccDetailsRequestData;
import com.vume.allinonegames.Model.JoshBankAccDetailsResponseData;
import com.vume.allinonegames.Model.JoshBuyInCallResponseData;
import com.vume.allinonegames.Model.JoshBuyinRequestData;
import com.vume.allinonegames.Model.JoshCancelWithdrawRequestData;
import com.vume.allinonegames.Model.JoshChangePassRequestData;
import com.vume.allinonegames.Model.JoshCloseTransactionRequest;
import com.vume.allinonegames.Model.JoshCloseTransactionResponseData;
import com.vume.allinonegames.Model.JoshFCMTokenUpdateRequestData;
import com.vume.allinonegames.Model.JoshFcmTokenUpdateResponseData;
import com.vume.allinonegames.Model.JoshFetchBalanceResponseData;
import com.vume.allinonegames.Model.JoshGenericOnlyStatusResponseData;
import com.vume.allinonegames.Model.JoshInitiateTransactionRequest;
import com.vume.allinonegames.Model.JoshInitiateTransactionResponseData;
import com.vume.allinonegames.Model.JoshInstallResponseData;
import com.vume.allinonegames.Model.JoshIsMobileNoRegisteredExistsResponseData;
import com.vume.allinonegames.Model.JoshKycResponseData;
import com.vume.allinonegames.Model.JoshLobbyCardListResponseData;
import com.vume.allinonegames.Model.JoshLoginRequestData;
import com.vume.allinonegames.Model.JoshLoginResponseData;
import com.vume.allinonegames.Model.JoshOffersResponseData;
import com.vume.allinonegames.Model.JoshOpenGamesResponseData;
import com.vume.allinonegames.Model.JoshProfileDetailsResponseData;
import com.vume.allinonegames.Model.JoshRaiseWithdrawRequestData;
import com.vume.allinonegames.Model.JoshRegisterRequestData;
import com.vume.allinonegames.Model.JoshRegisterResponseData;
import com.vume.allinonegames.Model.JoshResetPasswordRequestData;
import com.vume.allinonegames.Model.JoshUpdateEmailRequest;
import com.vume.allinonegames.Model.JoshUpdateUsernameEmailRequest;
import com.vume.allinonegames.Model.JoshUpdateUsernameRequest;
import com.vume.allinonegames.Model.JoshWithdrawRaiseOrcancelResponse;
import com.vume.allinonegames.Model.JoshWithdrawalTransactionsResponseData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JoshRummyServicesInterface {

    @Headers({
            "Content-Type: application/json",
    })
    @POST("/app/install")
    Call<JoshInstallResponseData> sendInstallData(@Header("X-Application") String serverApiKeys);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/signup")
    Call<JoshRegisterResponseData> sendRegisterData(@Header("X-Application") String serverApiKeys,
                                                    @Body JoshRegisterRequestData joshRegisterRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/signup")
    Call<JoshIsMobileNoRegisteredExistsResponseData> sendIfMobileNoAlreadyExist(@Header("X-Application") String serverApiKeys,
                                                                                @Query("accessToken") String firebaseAuthToken,
                                                                                @Query("install_key") String installKey,
                                                                                @Query("credential") String credential);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/signin")
    Call<JoshLoginResponseData> sendLoginData(@Header("X-Application") String serverApiKeys,
                                              @Body JoshLoginRequestData joshLoginRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/buyin")
    Call<JoshBuyInCallResponseData> sendBuyInData(@Header("X-Application") String serverApiKeys,
                                                  @Header("Authorization") String loginAuthToken,
                                                  @Body JoshBuyinRequestData joshBuyinRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/changePassword")
    Call<JoshGenericOnlyStatusResponseData> sendChangePasswordRequest(@Header("X-Application") String serverApiKeys,
                                                                      @Header("Authorization") String loginAuthToken,
                                                                      @Body JoshChangePassRequestData joshChangePassRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/update")
    Call<JoshFcmTokenUpdateResponseData> sendFcmTokenUpdateData(@Header("X-Application") String serverApiKeys,
                                                                @Body JoshFCMTokenUpdateRequestData joshFcmUpdateTokenRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/bankAcct")
    Call<JoshGenericOnlyStatusResponseData> sendBankAccDetailsData(@Header("X-Application") String serverApiKeys,
                                                                   @Header("Authorization") String loginAuthToken,
                                                                    @Body JoshBankAccDetailsRequestData joshBankAccDetailsRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/txns/init")
    Call<JoshInitiateTransactionResponseData> getInitiatePaytmTransactionApi(@Header("X-Application") String serverApiKeys,
                                                                             @Header("Authorization") String loginAuthToken,
                                                                             @Body JoshInitiateTransactionRequest joshInititateTransactionRequestdata);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/txns/close")
    Call<JoshCloseTransactionResponseData> getClosePaytmTransactionApi(@Header("X-Application") String serverApiKeys,
                                                                       @Header("Authorization") String loginAuthToken,
                                                                       @Body JoshCloseTransactionRequest joshInititateTransactionRequestdata);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/txns")
    Call<List<JoshAllTransactionsResponseData>> getAllTransactionsApi(@Header("X-Application") String serverApiKeys,
                                                                      @Header("Authorization") String loginAuthToken);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/resetPassword")
    Call<JoshGenericOnlyStatusResponseData> getResetPasswordApi(@Header("X-Application") String serverApiKeys,
                                                              @Body JoshResetPasswordRequestData joshResetPasswordRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/profile")
    Call<JoshGenericOnlyStatusResponseData> getUpdateEmailApi(@Header("X-Application") String serverApiKeys,
                                                                             @Header("Authorization") String loginAuthToken,
                                                                             @Body JoshUpdateEmailRequest joshUpdateEmailRequest);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/profile")
    Call<JoshGenericOnlyStatusResponseData> getUpdateProfileApi(@Header("X-Application") String serverApiKeys,
                                                              @Header("Authorization") String loginAuthToken,
                                                              @Body JoshUpdateUsernameRequest joshUpdateUsernameRequest);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/withdrawals")
    Call<JoshWithdrawRaiseOrcancelResponse> getRaiseWithdrawRequestApi(@Header("X-Application") String serverApiKeys,
                                                                       @Header("Authorization") String loginAuthToken,
                                                                       @Body JoshRaiseWithdrawRequestData joshRaiseWithdrawRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/withdrawals/cancel")
    Call<JoshWithdrawRaiseOrcancelResponse> getCancelWithdrawRequestApi(@Header("X-Application") String serverApiKeys,
                                                                       @Header("Authorization") String loginAuthToken,
                                                                       @Body JoshCancelWithdrawRequestData joshCancelWithdrawRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/withdrawals")
    Call<JoshWithdrawalTransactionsResponseData> getWithdrawRequestListApi(@Header("X-Application") String serverApiKeys,
                                                                           @Header("Authorization") String loginAuthToken);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/app/profile")
    Call<JoshGenericOnlyStatusResponseData> getUpdateUsernameEmailDetailsApi(@Header("X-Application") String serverApiKeys,
                                                                @Header("Authorization") String loginAuthToken,
                                                                @Body JoshUpdateUsernameEmailRequest joshUpdateUsernameEmailRequest);


    @Headers({
            "Content-Type: application/json"
    })
    @GET("/api/tables")
    Call<List<JoshLobbyCardListResponseData>> getRummyTables(@Header("X-Application") String serverApiKeys,
                                                             @Header("Authorization") String loginAuthToken,
                                                             @Query("lat") String currentLatitude,
                                                             @Query("long") String currentLongitude,
                                                             @Query("gameType") String gameType);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/balance")
    Call<JoshFetchBalanceResponseData> getRummyBalance(@Header("X-Application") String serverApiKeys,
                                                       @Header("Authorization") String loginAuthToken);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/profile")
    Call<JoshProfileDetailsResponseData> getProfileDetails(@Header("X-Application") String serverApiKeys,
                                                           @Header("Authorization") String loginAuthToken);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/app/bankAcct")
    Call<JoshBankAccDetailsResponseData> getBankAccDetails(@Header("X-Application") String serverApiKeys,
                                                           @Header("Authorization") String loginAuthToken);

    @GET("/app/offers")
    Call<List<JoshOffersResponseData>> getJoshOffers();

    @GET("/app/kyc")
    Call<List<JoshKycResponseData>> getKycDetails(@Header("X-Application") String serverApiKeys,
                                                  @Header("Authorization") String loginAuthToken);

    @Multipart
    @POST("/app/kyc")
    Call<ResponseBody> uploadKycDocument(@Header("X-Application") String serverApiKeys,
                              @Header("Authorization") String loginAuthToken,
                              @Part("idType") RequestBody idType,
                              @Part MultipartBody.Part file
    );

    @Multipart
    @POST("/app/profilePic")
    Call<ResponseBody> uploadProfilePic(@Header("X-Application") String serverApiKeys,
                                         @Header("Authorization") String loginAuthToken,
                                         @Part MultipartBody.Part file
    );

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/tickets")
    Call<List<JoshOpenGamesResponseData>> getOpenGames(@Header("X-Application") String serverApiKeys,
                                                      @Header("Authorization") String loginAuthToken,
                                                       @Query("gameType") String gameType);

    //    FANTASY LEAGUE APIS

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/api/matches")
    Call<List<FantasyAllMatchesResponseData>> getAllMatchesList(@Header("X-Application") String serverApiKeys,
                                                                @Header("Authorization") String loginAuthToken,
                                                                @Query("limit") String limit,
                                                                @Query("offset") String offset,
                                                                @Query("status") String status);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/api/my-matches")
    Call<List<FantasyAllMatchesResponseData>> getMyMatchesList(@Header("X-Application") String serverApiKeys,
                                                               @Header("Authorization") String loginAuthToken,
                                                               @Query("limit") String limit,
                                                               @Query("offset") String offset,
                                                               @Query("status") String status);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/api/contests")
    Call<List<FantasyContestsForMatchResponseData>> getAllContestsForTheMatch(@Header("X-Application") String serverApiKeys,
                                                                              @Header("Authorization") String loginAuthToken,
                                                                              @Query("match_id") int match_id);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("api/contests/{contest_id}")
    Call<FantasyContestDetailsWithMatchAndPlayerData> getContestDetailsIncludingMatchAndPlayerDetails(@Header("X-Application") String serverApiKeys,
                                                                                                      @Header("Authorization") String loginAuthToken,
                                                                                                      @Path("contest_id") int contest_id);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("/api/matches/{match_id}")
    Call<FantasyGetMatchDetailsResponseData> getMatchDetails(@Header("X-Application") String serverApiKeys,
                                                             @Header("Authorization") String loginAuthToken,
                                                             @Path("match_id") int match_id);

    @Headers({
            "Content-Type: application/json"
    })
    @GET("api/teams")
    Call<List<FantasyGetMyTeamsResponseData>> getMyTeamsForTheMatch(@Header("X-Application") String serverApiKeys,
                                                                    @Header("Authorization") String loginAuthToken,
                                                                    @Query("match_id") int match_id);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/api/teams")
    Call<FantasyCreateTeamResponseData> createMyFantasyTeam(@Header("X-Application") String serverApiKeys,
                                                            @Header("Authorization") String loginAuthToken,
                                                            @Body FantasyCreateTeamRequestData fantasyCreateTeamRequestData);

    @Headers({
            "Content-Type: application/json"
    })
    @POST("/api/contests/{contest_id}/join")
    Call<FantasyJoinContestResponseData> joinContest(@Header("X-Application") String serverApiKeys,
                                                     @Header("Authorization") String loginAuthToken,
                                                     @Path("contest_id") String contestId,
                                                     @Body FantasyJoinContestRequestData fantasyJoinContestRequestData);

}
