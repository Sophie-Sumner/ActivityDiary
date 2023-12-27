package de.rampro.activitydiary.net;


import de.rampro.activitydiary.queryVO.LoginResp;
import de.rampro.activitydiary.queryVO.SendVerificationResp;
import de.rampro.activitydiary.queryVO.Status;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRequest {
    @GET("/api/login/send_verification")
    Observable<SendVerificationResp> verify(@Query("email") String map);

    @POST("/api/login/signin")
    Observable<SendVerificationResp> register(@Body Map<String, String> map);

    @POST("/api/login/login")
    Observable<LoginResp> login(@Body Map<String, String> map);

    @GET("/api/login/get_users")
    Observable<Status> test(@Query("id") Integer id);

    //    @Multipart
//    @POST("/file/upload")
//    Observable<Result> uploadAvatar(@Part MultipartBody.Part mediaFile);

}
