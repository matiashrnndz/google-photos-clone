using System;

namespace Domain
{
    public class InvalidDate : Exception
    {
        override public string Message { get; }
        public InvalidDate()
        {
            Message = "Please enter a valid date";
        }
    }
}