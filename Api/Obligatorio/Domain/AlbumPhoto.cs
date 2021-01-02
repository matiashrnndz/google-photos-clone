using System;
using System.Collections.Generic;
using System.Text;

namespace Domain
{
    public class AlbumPhoto
    {
        public Guid PhotoId { get; set; }
        public Photo Photo { get; set; }

        public Guid AlbumId { get; set; }
        public Album Album { get; set; }
    }
}
