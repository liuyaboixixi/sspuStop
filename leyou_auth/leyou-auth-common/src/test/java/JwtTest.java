import org.junit.Before;
import org.junit.Test;
import pojo.UserInfo;
import utils.JwtUtils;
import utils.RsaUtils;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\视频\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\视频\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "DSUGF234DSAFDSA");
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
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU4MDEyOTM4Mn0.DE-7paNSCxtvhpwErdS9GiW1_HEdivsASFERB96dSpSv16HJjIiiZMvywP0eFkTRsFjXGMAPIjdxz-Q_omW3--Xk4tzu3PDsKPrcMb_VcWKfFT2Dk0muWUSV-mDqlqM8t7g4klLpyMPVsrFI_4MkvS8RwayUrxelGllkuoQRlHU";

        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
