using Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebApi.Models
{
    public class UserModel : Model<User, UserModel>
    {
        public Guid Id { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string PasswordConfirmation { get; set; }
        public string Name { get; set; }
        public DateTime BornDate { get; set; }
        public string Phone { get; set; }

        public UserModel() { }

        public UserModel(User entity)
        {
            SetModel(entity);
        }

        public override User ToEntity()
        {
            User user = new User()
            {
                Id = this.Id,
                Email = this.Email,
                Password = this.Password,
                PasswordConfirmation = this.PasswordConfirmation,
                Name = this.Name,
                BornDate = this.BornDate,
                Phone = this.Phone
            };
            return user;
        }

        protected override UserModel SetModel(User entity)
        {
            Id = entity.Id;
            Email = entity.Email;
            Name = entity.Name;
            BornDate = entity.BornDate;
            Phone = entity.Phone;
            return this;
        }
    }
}
