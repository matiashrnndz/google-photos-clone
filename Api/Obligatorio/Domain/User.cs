using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;

namespace Domain
{
    public class User
    {
        public Guid Id { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string PasswordConfirmation { get; set; }
        public string Name { get; set; }
        public DateTime BornDate { get; set; }
        public string Phone { get; set; }

        public List<Photo> Photos { get; set; }
        public List<Album> Albums { get; set; }

        public User()
        {
            Photos = new List<Photo>();
            Albums = new List<Album>();
        }

        public void Validate()
        {
            CheckValidEmail();
            CheckValidPasswordConfirmation();
            CheckValidPasswordFormat();
            CheckEmptyString();
            CheckValidDate();
            CheckValidPhone();
        }

        public void ValidateForUpdate()
        {
            CheckValidPasswordConfirmation();
            CheckValidPasswordFormat();
            CheckEmptyString();
            CheckValidDate();
            CheckValidPhone();
        }

        private void CheckValidPhone()
        {
            if (!Phone.All(char.IsDigit))
            {
                throw new InvalidPhone();
            }
        }

        private void CheckValidDate()
        {
            if (BornDate > DateTime.Now)
            {
                throw new InvalidDate();
            };
        }

        private void CheckEmptyString()
        {
            if (Name.Equals(""))
            {
                throw new InvalidName();
            }
        }

        private void CheckValidPasswordFormat()
        {
            if (Password.Length < 8)
            {
                throw new InvalidPasswordFormat();
            }
            if (!Password.Any(char.IsDigit))
            {
                throw new InvalidPasswordFormat();
            }
        }

        private void CheckValidPasswordConfirmation()
        {
            if (Password != PasswordConfirmation)
            {
                throw new InvalidPasswordConfirmation();
            }
        }

        private void CheckValidEmail()
        {
            try
            {
                new System.Net.Mail.MailAddress(Email);
            }
            catch (Exception)
            {
                throw new InvalidEmail();
            }
        }

        public void EncryptPassword()
        {
            this.Password = EncryptSHA256(this.Password);
            this.PasswordConfirmation = EncryptSHA256(this.PasswordConfirmation);
        }

        public static string EncryptSHA256(string phrase)
        {
            SHA256 encrypter = SHA256.Create();
            byte[] data = encrypter.ComputeHash(Encoding.UTF8.GetBytes(phrase));

            StringBuilder sBuilder = new StringBuilder();

            for (int i = 0; i < data.Length; i++)
            {
                sBuilder.Append(data[i].ToString("x2"));
            }

            return sBuilder.ToString();
        }
    }
}
