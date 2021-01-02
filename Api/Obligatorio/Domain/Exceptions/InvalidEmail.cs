using System;

namespace Domain
{
    public class InvalidEmail : Exception
    {
        override public string Message { get; }
        public InvalidEmail()
        {
            Message = "Please provide an email with this format: user@example.com";
        }
    }
}