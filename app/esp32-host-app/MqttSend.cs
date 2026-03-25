using System.Net;
using System.Security.Authentication;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Text.Json.Nodes;
using MQTTnet;

namespace esp32_host_app;

public class MqttSend
{
    private string broker = "101.133.231.235";
    private int port = 1883;
    private string clientId = Guid.NewGuid().ToString();
    private string topic = "431aec54-77c9-4822-a975-32ba203bec6d_monitor/data";
    private string username = "pc_client";
    private string password = "pc_client";


    private IMqttClient mqttClient;
    private MqttClientOptions options;

    public MqttSend()
    {


        
        var factory = new MqttClientFactory();
        mqttClient = factory.CreateMqttClient();
        options = new MqttClientOptionsBuilder()
            .WithTcpServer(broker, port) // MQTT broker address and port
            .WithClientId(clientId)
            .WithCredentials(username,password)
            .WithCleanSession()
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
            var payload = data.ToJsonString();

            if (!mqttClient.IsConnected)
            {
                ConnectAsync().Wait();
            }

            var message = new MqttApplicationMessageBuilder()
                .WithTopic(topic)
                .WithPayload(payload)
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
