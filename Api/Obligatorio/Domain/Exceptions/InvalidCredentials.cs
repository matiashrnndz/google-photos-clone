namespace Domain
{
    public class InvalidCredentials : System.Exception
    {
        override public string Message { get; }
        public InvalidCredentials()
        {
            Message = "These credentials were not found in the system";
        }
    }
}