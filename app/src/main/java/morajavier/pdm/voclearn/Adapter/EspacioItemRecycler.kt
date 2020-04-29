package morajavier.pdm.voclearn.Adapter

import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View


class EspacioItemRecycler(var verticalSpaceHeight : Int) : RecyclerView.ItemDecoration()
{


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        outRect.bottom = verticalSpaceHeight
    }
}