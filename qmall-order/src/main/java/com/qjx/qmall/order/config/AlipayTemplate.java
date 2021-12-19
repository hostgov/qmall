package com.qjx.qmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.qjx.qmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016110200786467";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaFeLhOJMc2v99ZjKsLb3PMmxxQ9/qMXjObmEd3nCRU1J/ySXtNLEacDukMKYSjii4BW60E5N9V5XRvFAyqshPi8+q7Kdw5pZHAHrGIs71klQIJVyuGyVGJ1a/lIPXEnfzLgIzo5RL3TN1VCOBUHz/7TePzshA70bmLXUuCldYix6VLouHdztM5rE5SF5ROAzVa4mZXl0s9MPLQIoFiaVkyVgYPZ3NS1neuBaWvqhYatoD08oIFx2mlJoXsoEnL2OYm/XCbgMgM2yhQohf9jM5AMx1GUINJM9aegSr1IRWNn+hDW/hS7hV4Me+jT+E9EgR+wB8eU7pBHNf/YN/YOnXAgMBAAECggEACodAT19MPgaAupLHF5iwhX3Ohxa6MAhdVs2X+TB/xI5K7sxRuRmGmGXezWM9lbr88UX9wHQt+byIUiJp4Gxm73iAIg+1iTNYRVW1DeDG/2SWNVBIOuVD8L0zYjfFomZyp70XDWw5780XfQbYvjc/s0UELOFWMYmn8IZeoVy3hycvL4qfxgUUKqhfIWZurzXTOvhhq0dl/rOnEA0Zqm7uyf5gf0/0X6tlHLQHvr3zNY+EG0bxZNkNo5+ZWd/JqHaVzixRr14gsSwuxWP8p0TFApVqpwka0R53cRMDvQTYFHoZm+PrPDTBZTcRl+pUEfNeKs1X5NLd+o/AfrtVe2/iAQKBgQDPB7rM3XNf/ADYouueZaRAQy0vtGmEB6CjaTjkr5gGzHBL+bojwVCzl9H4PUNJCJ2DUQfraKLY2IK/QodFtKH7cyYWQhmJ6V+UT5qoEWYLMQK3PRvybK6pkEY3tLqWBT7RZG4CLeI/z1zlknrR++0D70Dq9z7dBuyyACHIfLgkVwKBgQC+iDQZLpjTq6aD/b9q38WCqgw8L6vxYSWa2sKaRY84UzjGvAs2YYQm5nKc4E1pZr4JXIbFmFb4gO+cxyeZpYhcU4fyRTqEAHexWsJmWiTkfsOWDOX+qeDKsJCYe5WUmcorb6TaMwMphTsefFdCahNDyG3IGtoDWilqyzr19s32gQKBgBuC9nLk9sEJlv8y30bEUQhtJOMTAXaXIOS2ReY142j73+xoujCoLtY+1Zs8YOWirHPX38qLtCBG3VEXQSylLWVhzuzkScqG5bmqvm2ZKktQbwD0sPMNOES2NG6QqL7o4Ykn5GqLuGYsYzbquUGQTdXZBHJ0+riag/e/hr3CvSGvAoGBAI0QttH2J/x/1/ddhb2RE+DcGdbtzLYYIQl38iDaY2FmbGDnlL2ckYVjlXQEtgvVn9m6gaYGVk4C2GURJoahgkYpKjv7eYmF9xh+g/PXxfzOxexwfFCCYgZvl37t8R9mhcKHFFzg6/RvygeWM9zeQIks+ervxn444PVKpsq5AmEBAoGAPWnqmYOyibznc6DIrogziPcrrRRBmoZgnDz+0zx93JThTA8DMBp2XzwXFIrF9Ckx9zcFlmOctwtiUWC6w9t/ebQxs5CBtN/42LuNDwzlt8kCDDc+W5sWD/oW+SwOdmm/izq9S6QQYtGLTB45kM+FO4RhBGo1tACPqdgRGASdgls=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp3UUZS6hYHB7/FVwhrtZFIRntSAVm/OeWC8rBzO5aq53TChAnu/xr4MhAqvo+McRTwJHTgU9y34QYdQWzZUw5d7G3QgpT5FZ5V6ooDYYiZjG50ao2szetV+gFmBBCXBmhpOghQF1ZWTlRxKkKY9wzxQnI2LSvoNNL0FG+TovcvAxF/LGYKTuX2a6RM0rAwcwuIL4o4ROE/p9Cs31BG8y7Zl7/KgfZEmBmyufOo9/Wm3id7sC4VJSgix6d3fW0GbXX+wv8lKcYf9dJgPim5gHWD6ZAnjwdHQnS4NGA9BylTZjLDioyqyl7ziPNqVAUlLnso9GXZDj9UCX80E3yapRjQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.qmall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    //订单超时时间
    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
