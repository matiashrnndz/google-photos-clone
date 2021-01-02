using System;
using System.Collections.Generic;
using System.Text;

namespace GoogleAPIInterface
{
    public interface IGoogleServices
    {
        void UploadImage(string name, string fileType, string imageBase64);
        string GetImageURL(string name);
        List<string> GetImageTags(string name);
        void DeleteImage(string name);
    }
}
