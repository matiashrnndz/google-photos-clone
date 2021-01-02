using Domain;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WebApi.Models
{
    public class PhotoModel : Model<Photo, PhotoModel>
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Latitude { get; set; }
        public string Longitude { get; set; }
        public string Location { get; set; }
        public string Type { get; set; }
        public string URL { get; set; }
        public string ImageBase64 { get; set; }
        public DateTime TakenAt { get; set; }
        public List<TagModel> Tags { get; set; }

        public PhotoModel()
        {
            Tags = new List<TagModel>();
        }

        public PhotoModel(Photo entity)
        {
            SetModel(entity);
        }

        public override Photo ToEntity()
        {
            Photo photo = new Photo()
            {
                Id = this.Id,
                Name = this.Name,
                Latitude = this.Latitude,
                Longitude = this.Longitude,
                Location = this.Location,
                TakenAt = this.TakenAt,
                Type = this.Type,
                URL = this.URL,
                Tags = this.Tags.ConvertAll(t => t.ToEntity())
            };
            return photo;
        }

        protected override PhotoModel SetModel(Photo entity)
        {
            Id = entity.Id;
            Name = entity.Name;
            Latitude = entity.Latitude;
            Longitude = entity.Longitude;
            Location = entity.Location;
            TakenAt = entity.TakenAt;
            Type = entity.Type;
            URL = entity.URL;
            Tags = entity.Tags.ConvertAll(t => new TagModel(t));
            return this;
        }
    }
}
