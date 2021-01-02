using System;

namespace Domain
{
    public class InvalidName : Exception
    {
        override public string Message { get; }
        public InvalidName()
        {
            Message = "Please enter a name";
        }
    }
}