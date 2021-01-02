using BusinessLogic.Interface;
using DataAccess.Interface;
using Domain;
using GoogleAPIInterface;
using System;
using System.Collections.Generic;
using System.Linq;

namespace BusinessLogic
{
    public class PhotoLogic : IPhotoLogic
    {
        private IRepository<User> Users;
        private IRepository<Photo> Photos;
        private IGoogleServices GoogleServices;

        public PhotoLogic(IRepository<User> repository, IRepository<Photo> photos, IGoogleServices googleServices)
        {
            Users = repository;
            Photos = photos;
            GoogleServices = googleServices;
        }

        public Photo AddTag(Guid photoId, Tag tag)
        {
            Photo photoToUpdate = Photos.Get(photoId);
            photoToUpdate.Tags.Add(tag);
            return Photos.Update(photoToUpdate);
        }

        public void DeleteById(Guid id)
        {
            Photo photo = Photos.Get(id);
            GoogleServices.DeleteImage(photo.Name);
            Photos.Remove(photo);
            Photos.Save();
        }

        public Photo DeleteTag(Guid photoId, Guid tagId)
        {
            Photo photoToUpdate = Photos.Get(photoId);
            Tag tagToDelete = photoToUpdate.Tags.Find(t => t.Id == tagId);
            photoToUpdate.Tags.Remove(tagToDelete);
            return Photos.Update(photoToUpdate);
        }

        public bool ExistsById(Guid id)
        {
            return Photos.Exists(id);
        }

        public Photo GetById(Guid id)
        {
            return Photos.Get(id);
        }

        public List<Photo> SearchByTags(string email, List<Tag> tags)
        {
            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            return user.Photos.FindAll(p => p.Tags.Intersect(tags).Any());
        }
    }
}
