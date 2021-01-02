using BusinessLogic.Interface;
using DataAccess.Interface;
using Domain;
using GoogleAPIInterface;
using System;
using System.Collections.Generic;

namespace BusinessLogic
{
    public class UserLogic : IUserLogic
    {
        private IRepository<User> Users;
        private IGoogleServices GoogleServices;

        public UserLogic(IRepository<User> repository, IGoogleServices googleServices)
        {
            this.Users = repository;
            GoogleServices = googleServices;
        }

        public bool Exists(Guid id)
        {
            return Users.Exists(id);
        }

        public User Update(string email, User userToUpdate)
        {
            userToUpdate.ValidateForUpdate();
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            user.Password = userToUpdate.Password;
            user.PasswordConfirmation = userToUpdate.PasswordConfirmation;
            user.Name = userToUpdate.Name;
            user.BornDate = userToUpdate.BornDate;
            user.Phone = userToUpdate.Phone;
            user.EncryptPassword();
            return Users.Update(user);
        }

        public Photo AddPhoto(string email, Photo photoToUpload, string imageBase64)
        {
            Photo photo = UploadPhoto(email, photoToUpload, imageBase64);
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            user.Photos.Add(photo);
            Users.Update(user);
            return photo;
        }

        private Photo UploadPhoto(string email, Photo photoToUpload, string imageBase64)
        {
            DateTime now = DateTime.Now;
            string timestamp = now.Day.ToString() + now.Month.ToString() + now.Year.ToString()
                + "T" + now.Hour.ToString() + now.Minute.ToString() + now.Second.ToString() + now.Millisecond.ToString();
            string fileName = email + "$" + timestamp;
            try
            {
                GoogleServices.UploadImage(fileName, photoToUpload.Type, imageBase64);
                string fileURL = GoogleServices.GetImageURL(fileName);
                List<string> fileTags = GoogleServices.GetImageTags(fileURL);

                Photo photo = new Photo()
                {
                    Name = fileName,
                    TakenAt = now,
                    Latitude = photoToUpload.Latitude,
                    Longitude = photoToUpload.Longitude,
                    Location = photoToUpload.Location,
                    Type = photoToUpload.Type,
                    URL = fileURL,
                    Tags = fileTags.ConvertAll<Tag>(t => new Tag() { Name = t })
                };

                return photo;
            }
            catch (Exception)
            {
                throw new PhotoUpload();
            }
        }

        public bool ExistsPhoto(string email, Guid id)
        {
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            return user.Photos.Exists(p => p.Id == id);
        }

        public List<Photo> GetAllPhotos(string email)
        {
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            return user.Photos;
        }

        public List<Album> GetAllAlbums(string email)
        {
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            return user.Albums;
        }
    }
}