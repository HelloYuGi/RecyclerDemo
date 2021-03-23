package com.cadothy.recyclerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    val datas = ArrayList<String>()
    var count = 5
    lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        for (index in 0..count){
            datas.add(index.toString())
        }
        recyclerView = findViewById(R.id.recycler)
        recyclerView.addItemDecoration(DividerItemDecoration(this, HORIZONTAL))
//        recyclerView.layoutManager = LinearLayoutManager(this,HORIZONTAL,false)
        recyclerView.layoutManager = MMLayoutManager()
        adapter = MyAdapter(datas)
        recyclerView.adapter = adapter
        findViewById<Button>(R.id.add).setOnClickListener {
            count++
            datas.add(count.toString())
            adapter.notifyItemInserted(count-1)
        }
        findViewById<Button>(R.id.get).setOnClickListener {
            Log.e("wsw","size = " + recyclerView.childCount)
        }
    }
}


class MyAdapter(val data:List<String>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item1,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setText(data[position])
    }

}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun setText(txt:String){
        (itemView as TextView).text = txt
    }
}