using System;

namespace BusinessLogic.Interface
{
    public class PhotoUpload : Exception
    {
        override public string Message { get; }
        public PhotoUpload()
        {
            Message = "There was an error uploading your photo";
        }
    }
}