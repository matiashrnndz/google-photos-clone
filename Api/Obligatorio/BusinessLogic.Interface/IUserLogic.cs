using Domain;
using System;
using System.Collections.Generic;
using System.Text;

namespace BusinessLogic.Interface
{
    public interface IUserLogic
    {
        bool Exists(Guid id);
        User Update(string email, User userToUpdate);
        Photo AddPhoto(string email, Photo photoToUpload, string imageBase64);
        bool ExistsPhoto(string email, Guid id);
        List<Photo> GetAllPhotos(string email);
        List<Album> GetAllAlbums(string email);
    }
}