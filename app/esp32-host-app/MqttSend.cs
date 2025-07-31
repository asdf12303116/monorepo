using System.Net;
using System.Security.Authentication;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Text.Json.Nodes;
using MQTTnet;

namespace esp32_host_app;

public class MqttSend
{
    private string broker = "mfb03ea9.ala.cn-hangzhou.emqxsl.cn";
    private int port = 8883;
    private string clientId = Guid.NewGuid().ToString();
    private string topic = "monitor/data";
    private string username = "pc_client";
    private string password = "pc_client";

    private string cert = @"
-----BEGIN CERTIFICATE-----
MIIDrzCCApegAwIBAgIQCDvgVpBCRrGhdWrJWZHHSjANBgkqhkiG9w0BAQUFADBh
MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3
d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD
QTAeFw0wNjExMTAwMDAwMDBaFw0zMTExMTAwMDAwMDBaMGExCzAJBgNVBAYTAlVT
MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j
b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IENBMIIBIjANBgkqhkiG
9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4jvhEXLeqKTTo1eqUKKPC3eQyaKl7hLOllsB
CSDMAZOnTjC3U/dDxGkAV53ijSLdhwZAAIEJzs4bg7/fzTtxRuLWZscFs3YnFo97
nh6Vfe63SKMI2tavegw5BmV/Sl0fvBf4q77uKNd0f3p4mVmFaG5cIzJLv07A6Fpt
43C/dxC//AH2hdmoRBBYMql1GNXRor5H4idq9Joz+EkIYIvUX7Q6hL+hqkpMfT7P
T19sdl6gSzeRntwi5m3OFBqOasv+zbMUZBfHWymeMr/y7vrTC0LUq7dBMtoM1O/4
gdW7jVg/tRvoSSiicNoxBN33shbyTApOB6jtSj1etX+jkMOvJwIDAQABo2MwYTAO
BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA95QNVbR
TLtm8KPiGxvDl7I90VUwHwYDVR0jBBgwFoAUA95QNVbRTLtm8KPiGxvDl7I90VUw
DQYJKoZIhvcNAQEFBQADggEBAMucN6pIExIK+t1EnE9SsPTfrgT1eXkIoyQY/Esr
hMAtudXH/vTBH1jLuG2cenTnmCmrEbXjcKChzUyImZOMkXDiqw8cvpOp/2PV5Adg
06O/nVsJ8dWO41P0jmP6P6fbtGbfYmbW0W5BjfIttep3Sp+dWOIrWcBAI+0tKIJF
PnlUkiaY4IBIqDfv8NZ5YBberOgOzW6sRBc4L0na4UU+Krk2U886UAb3LujEV0ls
YSEY1QSteDwsOoBrp+uvFRTp2InBuThs4pFsiv9kuXclVzDAGySj4dzp30d8tbQk
CAUw7C29C79Fv1C5qfPrmAESrciIxpg0X40KPMbp1ZWVbd4=
-----END CERTIFICATE-----
";
    
    private new List<X509Certificate2> certificates;
    private IMqttClient mqttClient;
    private MqttClientOptions options;
    private MqttClientTlsOptions tlsOptions;

    public MqttSend()
    {

        var ca = PemCertificateLoader.LoadCertificateFromPemString(cert);
        certificates = new List<X509Certificate2> { ca };
        
        var factory = new MqttClientFactory();
        mqttClient = factory.CreateMqttClient();
        tlsOptions = new MqttClientTlsOptionsBuilder()
            .UseTls()
            .WithSslProtocols(SslProtocols.Tls12)
            .WithClientCertificates(certificates)
            .Build();
        options = new MqttClientOptionsBuilder()
            .WithTcpServer(broker, port) // MQTT broker address and port
            .WithCredentials(username, password) // Set username and password
            .WithClientId(clientId)
            .WithCleanSession()
            .WithTlsOptions(tlsOptions)
            .Build();
        
        // 连接到MQTT服务器
        ConnectAsync().Wait();
    }

    private async Task ConnectAsync()
    {
        try
        {
            await mqttClient.ConnectAsync(options);
            Console.WriteLine("已成功连接到MQTT服务器");
        }
        catch (Exception ex)
        {
            Console.WriteLine($"连接MQTT服务器失败: {ex.Message}");
        }
    }

    public async Task SendAsync(JsonObject data)
    {
        try
        {
            if (!mqttClient.IsConnected)
            {
                await ConnectAsync();
            }

            var message = new MqttApplicationMessageBuilder()
                .WithTopic(topic)
                .WithPayload(data.ToString())
                .WithQualityOfServiceLevel(MQTTnet.Protocol.MqttQualityOfServiceLevel.AtLeastOnce)
                .WithRetainFlag()
                .Build();

            await mqttClient.PublishAsync(message);
        }
        catch (Exception ex)
        {
            Console.WriteLine($"发送消息失败: {ex.Message}");
        }
    }

    // 静态方法包装器
    public static async Task send(JsonObject data)
    {
        var mqttSender = new MqttSend();
        await mqttSender.SendAsync(data);
    }
}