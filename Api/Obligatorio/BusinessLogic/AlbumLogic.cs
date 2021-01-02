using BusinessLogic.Interface;
using DataAccess.Interface;
using Domain;
using Domain.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BusinessLogic
{
    public class AlbumLogic : IAlbumLogic
    {
        private IRepository<User> Users;
        private IRepository<Photo> Photos;
        private IRepository<Album> Albums;

        public AlbumLogic(IRepository<User> repository, IRepository<Photo> photos, IRepository<Album> albums)
        {
            Users = repository;
            Photos = photos;
            Albums = albums;
        }


        public Album CreateAlbumByTags(string email, string name, List<Tag> tags)
        {
            CheckValidName(name);
            CheckExistentAlbumName(name);
            CheckTags(tags);

            User user = Users.GetAll().Find(u => u.Email.Equals(email));
            List<Photo> photos = user.Photos.FindAll(p => p.Tags.Intersect(tags).Any());
            Album album = new Album();
            album.Name = name;
            foreach (Photo photo in photos)
            {
                album.AddPhoto(photo);
            }
            user.Albums.Add(album);
            Users.Update(user);
            return album;
        }

        private void CheckExistentAlbumName(string name)
        {
            if (Albums.GetAll().Exists(a => a.Name == name))
            {
                throw new ExistentAlbumName();
            }
        }

        private void CheckValidName(string name)
        {
            if (name.Equals(""))
            {
                throw new NoAlbumName();
            }
        }

        private void CheckTags(List<Tag> tags)
        {
            if (tags.Count == 0)
            {
                throw new NoTags();
            }
        }

        public void DeleteAlbum(Guid id)
        {
            Albums.Remove(Albums.Get(id));
            Albums.Save();
        }

        public bool ExistsById(Guid id)
        {
            return Albums.Exists(id);
        }
    }
}
