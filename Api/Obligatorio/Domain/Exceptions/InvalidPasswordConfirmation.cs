using System;

namespace Domain
{
    public class InvalidPasswordConfirmation : Exception
    {
        override public string Message { get; }
        public InvalidPasswordConfirmation()
        {
            Message = "Please check that both passwords are the same";
        }
    }
}