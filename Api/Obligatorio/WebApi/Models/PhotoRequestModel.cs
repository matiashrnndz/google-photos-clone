using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebApi.Models
{
    public class PhotoRequestModel
    {
        public string FileType { get; set; }
        public string ImageBase64 { get; set; }
    }
}
