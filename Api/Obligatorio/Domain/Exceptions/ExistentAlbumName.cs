using System;
using System.Collections.Generic;
using System.Text;

namespace Domain.Exceptions
{
    public class ExistentAlbumName : Exception
    {
        override public string Message { get; }
        public ExistentAlbumName()
        {
            Message = "There is already an album with the same name";
        }
    }
}
