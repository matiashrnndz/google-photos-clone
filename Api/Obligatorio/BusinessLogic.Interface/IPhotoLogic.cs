using System;
using System.Collections.Generic;
using System.Text;
using Domain;

namespace BusinessLogic.Interface
{
    public interface IPhotoLogic
    {
        List<Photo> SearchByTags(string email, List<Tag> tags);
        Photo GetById(Guid id);
        void DeleteById(Guid id);
        bool ExistsById(Guid id);
        Photo AddTag(Guid photoId, Tag tag);
        Photo DeleteTag(Guid photoId, Guid tagId);
    }
}
