using Google.Apis.Auth.OAuth2;
using Google.Cloud.Storage.V1;
using Google.Cloud.Vision.V1;
using GoogleAPIInterface;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Http;
using System.Reflection;
using System.Resources;
using System.Text;
using System.Threading;

namespace GoogleAPI
{
    public class GoogleServices : IGoogleServices
    {
        private StorageClient Storage;
        private ImageAnnotatorClient ImageAnnotator;
        private UrlSigner UrlSigner;
        private string BucketName = "androidingdesoftware";

        public GoogleServices()
        {
            InitGoogleAPI();
        }

        private void InitGoogleAPI()
        {
            ResourceManager RM = new ResourceManager("GoogleAPI.Properties.Resources", Assembly.GetExecutingAssembly());

            byte[] credentialsFile = (byte[])RM.GetObject("credentials");
            string json = Encoding.UTF8.GetString(credentialsFile);

            GoogleCredential credential = GoogleCredential.FromJson(json);
            Storage = StorageClient.Create(credential);

            string path = System.IO.Path.GetTempPath() + "credentials.json";

            File.WriteAllBytes(path, Properties.Resources.credentials);

            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", path);
            ImageAnnotator = ImageAnnotatorClient.Create();

            ServiceAccountCredential serviceAccountCredential = ServiceAccountCredential.FromServiceAccountData(new MemoryStream(credentialsFile));
            UrlSigner = UrlSigner.FromServiceAccountCredential(serviceAccountCredential);
        }

        public void UploadImage(string name, string fileType, string imageBase64)
        {
            Stream image = new MemoryStream(Convert.FromBase64String(imageBase64));
            Storage.UploadObject(BucketName, name, fileType, image);
        }

        public string GetImageURL(string name)
        {
            return UrlSigner.Sign(BucketName, name, TimeSpan.FromDays(365), HttpMethod.Get);
        }

        public List<string> GetImageTags(string url)
        {
            List<string> annotations = new List<string>();
            var image = Image.FromUri(url);
            var response = ImageAnnotator.DetectLabels(image);
            foreach (var annotation in response)
            {
                annotations.Add(annotation.Description);
            }
            return annotations;
        }

        public void DeleteImage(string name)
        {
            Storage.DeleteObject(BucketName, name);
        }
    }
}
