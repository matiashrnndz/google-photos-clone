using System;
using System.Collections.Generic;
using System.Text;

namespace Domain.Exceptions
{
    public class NoAlbumName : Exception
    {
        override public string Message { get; }
        public NoAlbumName()
        {
            Message = "Please enter a valid album name";
        }
    }
}
