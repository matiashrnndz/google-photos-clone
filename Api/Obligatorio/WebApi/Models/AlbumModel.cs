using Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebApi.Models
{
    public class AlbumModel : Model<Album, AlbumModel>
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public List<PhotoModel> Photos { get; set; }

        public AlbumModel()
        {
            Photos = new List<PhotoModel>();
        }

        public AlbumModel(Album entity)
        {
            SetModel(entity);
        }

        public override Album ToEntity()
        {
            Album album = new Album()
            {
                Id = this.Id,
                Name = this.Name
            };
            foreach (PhotoModel photo in Photos)
            {
                album.AddPhoto(photo.ToEntity());
            }
            return album;
        }

        protected override AlbumModel SetModel(Album entity)
        {
            Id = entity.Id;
            Name = entity.Name;
            Photos = entity.Photos().ConvertAll(p => new PhotoModel(p));
            return this;
        }
    }
}
