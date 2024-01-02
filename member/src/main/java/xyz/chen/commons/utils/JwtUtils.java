package xyz.chen.commons.utils;

import cn.hutool.core.lang.UUID;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import xyz.chen.commons.base.UserInfo;

import java.security.SecureRandom;
import java.util.Date;

public class JwtUtils {
    private static final String sharedSecret = "31611159e7e6ff7843ea4627745e89225fc866621cfcfdbd40871af4413747cc";

    @SneakyThrows
    public static JWSSigner generateHmacJwsSigner() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(sharedSecret.getBytes());
        return new MACSigner(sharedSecret);
    }


    @SneakyThrows
    public static JWSVerifier getHmacJwsVerifier() {
        SecureRandom random = new SecureRandom();
        random.nextBytes(sharedSecret.getBytes());
        return new MACVerifier(sharedSecret);
    }

    @SneakyThrows
    public static String generateSignedJwt(String username,Long userId,String roles) {
        JWSSigner jwsSigner = generateHmacJwsSigner();
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        Date signTimeTime = new Date();
        Date expireTime = new Date(signTimeTime.getTime() + 1000L * 3600 * 2 );
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer("study-project")
                .subject(userId.toString())
                .expirationTime(expireTime)
                .issueTime(signTimeTime)
                .jwtID(UUID.fastUUID().toString())
                .claim("userName", username)
                .claim("roles", roles)
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        signedJWT.sign(jwsSigner);
        return  signedJWT.serialize();
    }
    @SneakyThrows
    public static Boolean verifySignedJwt(String token){
        JWSObject jwsObject = JWSObject.parse(token);
       return jwsObject.verify(getHmacJwsVerifier());
    }
    @SneakyThrows
    public static UserInfo getUserInfo(String token) {
         Boolean isVerify = verifySignedJwt(token);
         if (isVerify) {
             SignedJWT signedJWT = SignedJWT.parse(token);
             JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
             Long userId = Long.parseLong(claimsSet.getSubject());
             String userName = (String) claimsSet.getClaim("userName");
             String roles = (String) claimsSet.getClaim("roles");
             return new UserInfo(userId, userName, roles);
         } else {
             throw new RuntimeException("token验证失败");
         }
    }


}
