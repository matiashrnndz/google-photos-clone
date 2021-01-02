using System;

namespace Domain
{
    public class InvalidPhone : Exception
    {
        override public string Message { get; }
        public InvalidPhone()
        {
            Message = "Please enter a phone with only numbers";
        }
    }
}