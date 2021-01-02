using System;
using System.Collections.Generic;
using System.Text;

namespace Domain
{
    public class Photo
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Type { get; set; }
        public string URL { get; set; }
        public DateTime TakenAt { get; set; }
        public string Latitude { get; set; }
        public string Longitude { get; set; }
        public string Location { get; set; }

        public User User { get; set; }
        public List<Tag> Tags { get; set; }
        public List<AlbumPhoto> AlbumPhotos { get; set; }

        public Photo()
        {
            Tags = new List<Tag>();
            AlbumPhotos = new List<AlbumPhoto>();
        }
    }
}
