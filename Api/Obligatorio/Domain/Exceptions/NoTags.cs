using System;
using System.Collections.Generic;
using System.Text;

namespace Domain.Exceptions
{
    public class NoTags : Exception
    {
        override public string Message { get; }
        public NoTags()
        {
            Message = "Please enter at least one tag";
        }
    }
}
