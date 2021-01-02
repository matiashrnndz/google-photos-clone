using Domain;
using System;
using System.Collections.Generic;
using System.Text;

namespace BusinessLogic.Interface
{
    public interface IAlbumLogic
    {
        Album CreateAlbumByTags(string email, string name, List<Tag> tags);
        void DeleteAlbum(Guid id);
        bool ExistsById(Guid id);
    }
}
