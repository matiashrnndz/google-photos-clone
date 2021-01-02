using System;

namespace Domain
{
    public class InvalidPasswordFormat : Exception
    {
        override public string Message { get; }
        public InvalidPasswordFormat()
        {
            Message = "Please enter an alphanumeric password with at least 8 characters";
        }
    }
}