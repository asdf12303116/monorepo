using System.Text.Json.Nodes;
using LibreHardwareMonitor.Hardware;
using RestSharp;

namespace esp32_host_app;

public class HttpSend
{
    public static async Task  send(RestClient restClient,JsonObject data)
    {
        var url = "/update_status";

        try
        {
            var sendJson = restClient.PostJsonAsync(url, data);
            await sendJson;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
        }
        
    }
}