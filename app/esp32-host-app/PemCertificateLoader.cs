using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;

namespace esp32_host_app;

public class PemCertificateLoader
{
    public static X509Certificate2? LoadCertificateFromPemString(string pemString)
    {
        // 移除PEM字符串的头部和尾部，以及所有空白字符
        string base64Content = pemString
            .Replace("-----BEGIN CERTIFICATE-----", "")
            .Replace("-----END CERTIFICATE-----", "")
            .Trim();

        try
        {
            // 将Base64字符串解码为字节数组
            byte[] certBytes = Convert.FromBase64String(base64Content);

            // 使用字节数组创建X509Certificate2对象
            X509Certificate2 certificate = new X509Certificate2(certBytes);
            return certificate;
        }
        catch (FormatException ex)
        {
            Console.WriteLine($"PEM字符串格式错误: {ex.Message}");
            return null;
        }
        catch (CryptographicException ex)
        {
            Console.WriteLine($"加载证书失败: {ex.Message}");
            return null;
        }
    }

}
