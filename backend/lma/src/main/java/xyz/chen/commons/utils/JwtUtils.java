package xyz.chen.commons.utils;

import cn.hutool.core.lang.UUID;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.SneakyThrows;
import xyz.chen.commons.base.OAuthUserInfo;
import xyz.chen.commons.base.UserInfo;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

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
    public static String generateSignedJwt(String username, Long userId, String roles) {
        JWSSigner jwsSigner = generateHmacJwsSigner();
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        Date signTimeTime = new Date();
        Date expireTime = new Date(signTimeTime.getTime() + 1000L * 3600 * 2);
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
        return signedJWT.serialize();
    }

    @SneakyThrows
    public static Boolean verifySignedJwt(String token) {
        JWSObject jwsObject = JWSObject.parse(token);
        return jwsObject.verify(getHmacJwsVerifier());
    }

    @SneakyThrows
    private static JWTClaimsSet getJWTClaimsSet(String token, Boolean needCheck) {
        if (!needCheck) {
            return getJWTClaimsSetWithOutCheck(token);
        }
        Boolean isVerify = verifySignedJwt(token);
        if (isVerify) {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            expirationTimeCheck(claimsSet);
            return claimsSet;
        } else {
            throw new RuntimeException("token解析失败");
        }
    }

    private static void expirationTimeCheck(JWTClaimsSet claimsSet) {
        try {
            Date expirationTime = claimsSet.getExpirationTime();
            Date now = new Date();
            if (now.after(expirationTime)) {
                throw new RuntimeException("token已过期");
            }
        } catch (Exception e) {
            throw new RuntimeException("token已过期或已失效");
        }
    }

    @SneakyThrows
    private static JWTClaimsSet getJWTClaimsSetWithOutCheck(String token) {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        return claimsSet;
    }


    @SneakyThrows
    public static UserInfo getUserInfo(String token) {
        JWTClaimsSet claimsSet = getJWTClaimsSet(token, true);
        Long userId = Long.parseLong(claimsSet.getSubject());
        String userName = (String) claimsSet.getClaim("userName");
        String roles = (String) claimsSet.getClaim("roles");
        return new UserInfo(userId, userName, roles);
    }

    @SneakyThrows
    public static OAuthUserInfo getOAuthUserInfo(String token) {
        JWTClaimsSet claimsSet = getJWTClaimsSet(token, false);
        String userUUID = (String) claimsSet.getClaim("oid");
        String email = (String) claimsSet.getClaim("email");
        String userName = (String) claimsSet.getClaim("preferred_username");
        String showName = (String) claimsSet.getClaim("name");
        List<String> groups = (List<String>) claimsSet.getClaim("groups");
        return new OAuthUserInfo(userName, showName, userUUID, email, groups);
    }


}
