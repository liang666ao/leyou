import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "D:\\tmp\\rsa\\rsa.pub";

    private static final String priKeyPath = "D:\\tmp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU3MDM0NTkyMH0.apfMIlrBOWDLiKHZtbNb2s8GUOgXg5Jmv2tz5FMjuhl0ZnpeobCQqxWsXp51wST-KKt6QnAz7-1_DdzkOE-5g8os6f7JknCl4OVwQOXNoL0dmvWWpUr-hBefFFR1Jt8dfNot9LuJKGlHJZu-2-Fj4bskNg737-B732LIle4MhX8";
        // 解析token
        UserInfo user = JwtUtils.getUserInfo(publicKey,token);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getName());
    }
}
